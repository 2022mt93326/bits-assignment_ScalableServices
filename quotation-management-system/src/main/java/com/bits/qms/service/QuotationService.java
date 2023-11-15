package com.bits.qms.service;

import com.bits.qms.client.GatewayClient;
import com.bits.qms.entity.Quotation;
import com.bits.qms.exception.NotFoundException;
import com.bits.qms.exception.ValidationException;
import com.bits.qms.models.QuotationDto;
import com.bits.qms.models.QuotationResponseDto;
import com.bits.qms.repository.QuotationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuotationService {
    @Autowired
    private final QuotationRepository quotationRepository;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final GatewayClient gatewayClient;

    public QuotationService(QuotationRepository quotationRepository,GatewayClient gatewayClient,ModelMapper modelMapper) {
        this.quotationRepository = quotationRepository;
        this.gatewayClient = gatewayClient;
        this.modelMapper = modelMapper;
    }
    public QuotationResponseDto createQuotation(QuotationDto quotationDto, String token) {
        if (isValidProductId(quotationDto.getProductId(), token)) {
            return modelMapper.map(quotationRepository
                    .save(modelMapper.map(quotationDto, Quotation.class)), QuotationResponseDto.class);
        }
        throw new ValidationException("ProductId:" + quotationDto.getProductId() + " is not valid");
    }

    public QuotationResponseDto getQuotation(String id) {
        return quotationRepository.findById(id)
                .map(quotation -> modelMapper.map(quotation, QuotationResponseDto.class))
                .orElseThrow(() -> new NotFoundException("Quotation not found by id:" + id));
    }

    public void deleteQuotation(String id) {
        quotationRepository.deleteById(id);
    }

    private boolean isValidProductId(String productId, String token) {
        return gatewayClient.getProductById(productId, token).isPresent();
    }

}
