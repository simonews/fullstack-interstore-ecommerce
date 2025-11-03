package com.example.demo;

import com.example.demo.entity.Prodotto;
import com.example.demo.repository.ProdottoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ProdottoRepository repo) {
        return args -> {
            Prodotto p = new Prodotto();
            p.setNome("maglia inter");
            p.setPrezzo(89.99);
            repo.save(p);
        };
    }
}
