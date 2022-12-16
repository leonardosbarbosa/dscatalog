package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.utils.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private Long existentId;
    private Long nonExistentId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;
    ProductDTO productDTO;
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        existentId = 1L;
        nonExistentId = 1000L;
        dependentId = 4L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(product));
        productDTO = Factory.createProductDTO();

        // findAll paged
        Mockito.when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        Mockito.when(productRepository.find(any(),any(), any())).thenReturn(page);
        // save
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);
        //findById
        Mockito.when(productRepository.findById(existentId)).thenReturn(Optional.of(product));
        // findById with nonexistent id
        Mockito.when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        // update sucessful
        Mockito.when(productRepository.getOne(existentId)).thenReturn(product);
        Mockito.when(categoryRepository.getOne(existentId)).thenReturn(category);
        // update with nonexistent id
        Mockito.when(productRepository.getOne(nonExistentId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(categoryRepository.getOne(nonExistentId)).thenThrow(EntityNotFoundException.class);
        // successful delete
        Mockito.doNothing().when(productRepository).deleteById(existentId);
        // delete with nonexistent id
        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistentId);
        // delete with dependent id
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable, 0L, "");
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existentId);
        Assertions.assertNotNull(result);
        Mockito.verify(productRepository).findById(existentId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistentId));
        Mockito.verify(productRepository).findById(nonExistentId);
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.update(existentId, productDTO);
        Assertions.assertNotNull(result);
        Mockito.verify(productRepository).getOne(existentId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistentId, productDTO));
        Mockito.verify(productRepository).getOne(nonExistentId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> service.delete(existentId));
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(existentId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistentId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
    }
}
