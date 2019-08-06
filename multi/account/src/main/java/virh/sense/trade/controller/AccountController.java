package virh.sense.trade.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import virh.sense.trade.service.AccountService;

@RequestMapping("/account")
@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/check")
	@ResponseBody
	public boolean checkBalanceEnough(@RequestParam Long accountId, @RequestParam BigDecimal price) {
		return accountService.checkBalanceEnough(accountId, price);
	}

	@GetMapping("/prepare/decrement")
	public void prepareBalanceDecrement(@RequestParam Long accountId, @RequestParam BigDecimal price) {
		accountService.prepareBalanceDecrement(accountId, price);
	}

	@GetMapping("/prepare/revert")
	public void prepareBalanceRevert(@RequestParam Long accountId, @RequestParam BigDecimal price) {
		accountService.prepareBalanceRevert(accountId, price);
	}

	@GetMapping("/query")
	@ResponseBody
	public BigDecimal queryBalance(@RequestParam Long accountId) {
		return accountService.queryBalance(accountId);
	}

	@GetMapping("/execute/decrement")
	public void executeBalanceDecrement(@RequestParam Long accountId, @RequestParam BigDecimal price) {
		accountService.executeBalanceDecrement(accountId, price);
	}
	
}
