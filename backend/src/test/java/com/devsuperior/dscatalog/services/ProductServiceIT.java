package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    private Long existentId;
    private Long nonExistentId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existentId = 1L;
        nonExistentId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        productService.delete(existentId);
        Assertions.assertEquals(productRepository.count(), countTotalProducts - 1);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.delete(nonExistentId));
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPageExists() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDTO> result = productService.findAllPaged(pageRequest);
        Assertions.assertTrue(result.hasContent());
        Assertions.assertEquals(result.getNumber(), pageRequest.getPageNumber());
        Assertions.assertEquals(result.getSize(), pageRequest.getPageSize());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(50, 10);
        Page<ProductDTO> result = productService.findAllPaged(pageRequest);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDTO> result = productService.findAllPaged(pageRequest);
        Assertions.assertTrue(result.hasContent());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }
}
