package org.zcj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zcj.rpc.client.config.EnableRpcClient;

import java.io.IOException;


@SpringBootApplication()
@EnableRpcClient()
public class ClientApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ClientApplication.class, args);
	}

}
