package com.bits.qms;

import com.bits.qms.client.GatewayClient;
import com.bits.qms.entity.Quotation;
import com.bits.qms.exception.NotFoundException;
import com.bits.qms.exception.ValidationException;
import com.bits.qms.models.Coverage;
import com.bits.qms.models.ProductDto;
import com.bits.qms.models.QuotationDto;
import com.bits.qms.repository.QuotationRepository;
import com.bits.qms.service.QuotationService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuotationServiceTests {
    @MockBean
    private final QuotationRepository quotationRepository = mock(QuotationRepository.class);
    @MockBean
    private final GatewayClient gatewayClient = mock(GatewayClient.class);
    private final ModelMapper modelMapper = new ModelMapper();

    private final QuotationService quotationService = new QuotationService(quotationRepository, gatewayClient, modelMapper);

    @Test
    public void whenQuotationIsSavedSuccessfullyReturnsQuotationDetails() {
        Coverage coverageDto = Coverage.builder()
                .code("C1")
                .name("coverage1")
                .description("coverage desc")
                .build();
        QuotationDto quotationDto = QuotationDto.builder()
                .lob("vehicle")
                .productId("1234")
                .coverages(singletonList(coverageDto))
                .productCharacteristics(emptyList())
                .plan("silver")
                .sumAssured(1000)
                .totalPremium(100)
                .build();

        ProductDto productDto = ProductDto.builder()
                .lob("vehicle")
                .name("test")
                .description("some desc")
                .build();
        when(quotationRepository.save(any(Quotation.class))).thenReturn(modelMapper.map(quotationDto, Quotation.class));
        when(gatewayClient.getProductById("1234", "1234")).thenReturn(Optional.of(productDto));
        QuotationDto createdQuotation = quotationService.createQuotation(quotationDto, "1234");
        Assert.assertEquals(createdQuotation.getProductId(), quotationDto.getProductId());

    }

    @Test
    public void shouldThrowErrorWhenProductIdIsInvalid() {
        Coverage coverageDto = Coverage.builder()
                .code("C1")
                .name("coverage1")
                .description("coverage desc")
                .build();
        QuotationDto quotationDto = QuotationDto.builder()
                .lob("vehicle")
                .productId("1234")
                .coverages(singletonList(coverageDto))
                .productCharacteristics(emptyList())
                .plan("silver")
                .sumAssured(1000)
                .totalPremium(100)
                .build();

        when(quotationRepository.save(any(Quotation.class))).thenReturn(modelMapper.map(quotationDto, Quotation.class));
        when(gatewayClient.getProductById("1234", "1234")).thenReturn(Optional.empty());
        Assert.assertThrows(ValidationException.class, () -> quotationService.createQuotation(quotationDto, "1234"));

    }

    @Test
    public void whenQuotationIdIsValidReturnsQuotationDetails() {
        Coverage coverageDto = Coverage.builder()
                .code("C1")
                .name("coverage1")
                .description("coverage desc")
                .build();
        QuotationDto quotationDto = QuotationDto.builder()
                .lob("vehicle")
                .productId("1234")
                .coverages(singletonList(coverageDto))
                .productCharacteristics(emptyList())
                .plan("silver")
                .sumAssured(1000)
                .totalPremium(100)
                .build();

        when(quotationRepository.findById("1234")).thenReturn(Optional.of(modelMapper.map(quotationDto, Quotation.class)));
        QuotationDto createdQuotation = quotationService.getQuotation("1234");
        Assert.assertEquals(createdQuotation.getProductId(), quotationDto.getProductId());

    }

    @Test
    public void whenQuotationIdIsInvalidThrowsError() {

        when(quotationRepository.findById("1234")).thenReturn(Optional.empty());
        Assert.assertThrows(NotFoundException.class, () -> quotationService.getQuotation("1234"));

    }

}
