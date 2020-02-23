package com.gcaldas.conceptualmodel.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gcaldas.conceptualmodel.domain.OrderItem;
import com.gcaldas.conceptualmodel.domain.PaymentWithBoleto;
import com.gcaldas.conceptualmodel.domain.PurchaseOrder;
import com.gcaldas.conceptualmodel.domain.enums.PaymentStatus;
import com.gcaldas.conceptualmodel.repositories.ClientRepository;
import com.gcaldas.conceptualmodel.repositories.OrderItemRepository;
import com.gcaldas.conceptualmodel.repositories.PaymentRepository;
import com.gcaldas.conceptualmodel.repositories.ProductRepository;
import com.gcaldas.conceptualmodel.repositories.PurchaseOrderRepository;
import com.gcaldas.conceptualmodel.services.exceptions.ObjectNotFoundException;

@Service
public class PurchaseOrderService {

	@Autowired
	private PurchaseOrderRepository rep;

	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private EmailService emailService;
	
	public PurchaseOrder find(Integer id) {
		Optional<PurchaseOrder> obj = rep.findById(id);

		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Object not found. Id: " + id + ", Type: " + PurchaseOrder.class.getName()));
	}
	
	@Transactional
	public PurchaseOrder insert(PurchaseOrder obj) {
		obj.setId(null);
		obj.setOrderDate(new Date());
		obj.setClient(clientService.find(obj.getClient().getId()));
		obj.getPayment().setStatus(PaymentStatus.PENDING);
		obj.getPayment().setOrderNumber(obj);
		if(obj.getPayment() instanceof PaymentWithBoleto) {
			PaymentWithBoleto paid = (PaymentWithBoleto) obj.getPayment();
			boletoService.fillPaymentWithBoleto(paid, obj.getOrderDate());
		}
		
		obj = rep.save(obj);
		paymentRepository.save(obj.getPayment());
		for (OrderItem orderItem : obj.getItems()) {
			orderItem.setDiscount(0.0);
			orderItem.setProduct(productService.find(orderItem.getProduct().getId()));
			orderItem.setPrice(orderItem.getProduct().getPrice());
			orderItem.setPurchaseOrder(obj);
		}
		orderItemRepository.saveAll(obj.getItems());
		emailService.sendOrderConfirmationHtmlEmail(obj);
		return obj;
	}
}