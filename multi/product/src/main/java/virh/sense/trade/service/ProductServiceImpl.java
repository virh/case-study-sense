package virh.sense.trade.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import virh.sense.trade.domain.Product;
import virh.sense.trade.service.ProductServiceGrpc.ProductServiceImplBase;

@Service(timeout=5000, version="0.0.1")
public class ProductServiceImpl extends ProductServiceImplBase implements ProductService {

	@Autowired
	ProductRepository productRepository;
	
	Map<Long, Long> stockMap = new HashMap<>();
	
	public boolean checkStockEnough(Long productId, Long number) {
		if (!stockMap.containsKey(productId)) {
			Optional<Product> product = productRepository.findById(productId);
			if (!product.isPresent()) {
				return false;
			}
			stockMap.put(productId, product.get().getNumber());
		}
		return stockMap.get(productId) >= number;
	}
	
	
	public void prepareStockDecrement(Long productId, Long number) {
		stockMap.put(productId, stockMap.get(productId)-number);
	}
	
	public void prepareStockRevert(Long productId, Long number) {
		stockMap.put(productId, stockMap.get(productId)+number);
	}
	
	public Long queryStock(Long productId) {
		return stockMap.get(productId);
	}


	@Override
	public void executeStockDecrement(Long productId, Long number) {
		Product product = productRepository.findById(productId).get();
		product.setNumber(product.getNumber()-number);
		productRepository.save(product);
	}


	@Override
	public void checkStockEnough(ProductAndNumberRequest request, StreamObserver<CheckResponse> responseObserver) {
		boolean flag = checkStockEnough(request.getProductId(), request.getNumber());
		CheckResponse response = CheckResponse.newBuilder()
				.setFlag(flag)
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}


	@Override
	public void prepareStockDecrement(ProductAndNumberRequest request, StreamObserver<EmptyResponse> responseObserver) {
		prepareStockDecrement(request.getProductId(), request.getNumber());
		EmptyResponse response = EmptyResponse.newBuilder()
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}


	@Override
	public void prepareStockRevert(ProductAndNumberRequest request, StreamObserver<EmptyResponse> responseObserver) {
		prepareStockRevert(request.getProductId(), request.getNumber());
		EmptyResponse response = EmptyResponse.newBuilder()
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}


	@Override
	public void queryStock(ProductRequest request, StreamObserver<ProductQueryResponse> responseObserver) {
		long number = queryStock(request.getProductId());
		ProductQueryResponse response = ProductQueryResponse.newBuilder()
				.setNumber(number)
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}


	@Override
	public void executeStockDecrement(ProductAndNumberRequest request, StreamObserver<EmptyResponse> responseObserver) {
		executeStockDecrement(request.getProductId(), request.getNumber());
		EmptyResponse response = EmptyResponse.newBuilder()
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
}
