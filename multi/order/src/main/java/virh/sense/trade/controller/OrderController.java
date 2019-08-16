package virh.sense.trade.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import virh.sense.trade.service.OrderService;

@RequestMapping("/order")
@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/buy")
	@ResponseBody
	public boolean checkBalanceEnough(@RequestParam Long productId, @RequestParam Long accountId, @RequestParam Long number, @RequestParam BigDecimal price) {
		return orderService.buy(productId, accountId, number, price);
	}
	
}
