package virh.sense.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import virh.sense.trade.service.ProductService;

@RequestMapping("/product")
@RestController
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/check")
	public boolean checkStockEnough(@RequestParam Long productId, @RequestParam Long number) {
		return productService.checkStockEnough(productId, number);
	}

	@GetMapping("/prepare/decrement")
	public void prepareStockDecrement(@RequestParam Long productId, @RequestParam Long number) {
		productService.prepareStockDecrement(productId, number);
	}

	@GetMapping("/prepare/revert")
	public void prepareStockRevert(@RequestParam Long productId, @RequestParam Long number) {
		productService.prepareStockRevert(productId, number);
	}

	@GetMapping("/query")
	public Long queryStock(@RequestParam Long productId) {
		return productService.queryStock(productId);
	}

	@GetMapping("/execute/decrement")
	public void executeStockDecrement(@RequestParam Long productId, @RequestParam Long number) {
		productService.executeStockDecrement(productId, number);
	}
	
}
