package com.example.PCOnlineShop.repository.product;

import com.example.PCOnlineShop.model.product.ExternalProductSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalProductSourceRepository extends JpaRepository<ExternalProductSource, Integer> {

    Optional<ExternalProductSource> findByProviderCodeIgnoreCaseAndExternalId(String providerCode, String externalId);
}
