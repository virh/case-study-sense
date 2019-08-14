package virh.sense.trade.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product", path="product")
public interface ProductServiceClient extends ProductService {

	@GetMapping("/check")
	public boolean checkStockEnough(@RequestParam("productId") Long productId, @RequestParam("number") Long number);
	
	@GetMapping("/prepare/decrement")
	public void prepareStockDecrement(@RequestParam("productId") Long productId, @RequestParam("number") Long number);
	
	@GetMapping("/prepare/revert")
	public void prepareStockRevert(@RequestParam("productId") Long productId, @RequestParam("number") Long number);
	
	@GetMapping("/query")
	public Long queryStock(@RequestParam("productId") Long productId);
	
	@GetMapping("/execute/decrement")
	public void executeStockDecrement(@RequestParam("productId") Long productId, @RequestParam("number") Long number);
	

}
