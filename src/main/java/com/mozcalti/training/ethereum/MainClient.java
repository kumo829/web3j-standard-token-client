package com.mozcalti.training.ethereum;

import java.math.BigInteger;

import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import com.mozcalti.training.ethereum.generated.StandardToken;

public class MainClient {

	private static String CONTRACT_ADDRESS = "Address";
	private static String PRIVATE_KEY_FILE = "";
	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

	public static void main(String... args) throws Exception {
		Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/token")); // defaults to
																										// http://localhost:8545/

		web3j.web3ClientVersion().observable()
				.subscribe(v -> System.out.println("version: " + v.getWeb3ClientVersion()));

		Credentials credentials = Credentials
				.create("privateKey"); 
//		Credentials credentials = Credentials.create("private key");

		StandardToken standardToken = StandardToken.load(CONTRACT_ADDRESS, web3j, credentials, GAS_PRICE, GAS_LIMIT);
		standardToken.balanceOf("address").observable()
				.subscribe(result -> System.out.println(result));
		standardToken.balanceOf("address").observable()
				.subscribe(result -> System.out.println(result));

		
		EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, standardToken.getContractAddress().substring(2));
		
		String encodedEventSignature = EventEncoder.encode(StandardToken.TRANSFER_EVENT);
		filter.addSingleTopic(encodedEventSignature);
		web3j.ethLogObservable(filter).subscribe(eventString -> System.out.println("event string={}" + eventString.toString()));
		
		standardToken.transfer("address", BigInteger.valueOf(2L)).observable()
				.subscribe(result -> System.out.println(result.getTransactionHash()));
		
		web3j.ethLogObservable(filter).subscribe(log -> log.toString());

		standardToken.balanceOf("address").observable()
				.subscribe(result -> System.out.println(result));
		standardToken.balanceOf("address").observable()
				.subscribe(result -> System.out.println(result));

	}
}
