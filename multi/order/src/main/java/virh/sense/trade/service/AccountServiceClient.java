package virh.sense.trade.service;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account", path="account")
public interface AccountServiceClient extends AccountService {

	@GetMapping(value = "/check")
	public boolean checkBalanceEnough(@RequestParam("accountId") Long accountId, @RequestParam("price") BigDecimal price);
	
	@GetMapping("/prepare/decrement")
	public void prepareBalanceDecrement(@RequestParam("accountId") Long accountId, @RequestParam("price") BigDecimal price);
	
	@GetMapping("/prepare/revert")
	public void prepareBalanceRevert(@RequestParam("accountId") Long accountId, @RequestParam("price") BigDecimal price);
	
	@GetMapping("/query")
	public BigDecimal queryBalance(@RequestParam("accountId") Long accountId);
	
	@GetMapping("/execute/decrement")
	public void executeBalanceDecrement(@RequestParam("accountId") Long accountId, @RequestParam("price") BigDecimal price);
	
}
