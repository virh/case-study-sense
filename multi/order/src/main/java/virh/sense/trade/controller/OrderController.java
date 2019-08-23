package virh.sense.trade.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import virh.sense.trade.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	OrderService orderService;
	
	@ResponseBody
	@GetMapping("/buy")
	boolean buy(@RequestParam Long productId, @RequestParam Long accountId, @RequestParam Long number, @RequestParam BigDecimal price) {
		return orderService.buy(productId, accountId, number, price);
	}
}
