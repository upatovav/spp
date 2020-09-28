package com.github.upatovav.spp.testtask;

import com.github.upatovav.spp.testtask.dto.TransactionDto;
import com.github.upatovav.spp.testtask.dto.ValidationErrorDto;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Log
public class Controller {

    private FunctionResultCache<TransactionDto, ValidationErrorDto> functionResultCache = new FunctionResultCache<>();

    private ResultCache<TransactionDto, ValidationErrorDto> resultCache = new ResultCache<>();

    @Autowired
    Validator validator;

    @PostMapping("/validateWithSpring")
    @ResponseBody
    public ResponseEntity<String> validateWithSpring(@RequestBody  @Valid TransactionDto transaction){
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validateWithFunctionAndArgCache")
    @ResponseBody
    public ResponseEntity<Object> validateWithFunctionAndArgCache(@RequestBody TransactionDto transaction) throws ExecutionException, InterruptedException {
        ValidationErrorDto dto = functionResultCache.compute(transaction, new FunctionResultCache.CacheableFunction<>() {
            @Override
            public ValidationErrorDto apply(TransactionDto transactionDto) {
                log.info("cacheable function for argument and function cache validating " + transactionDto);
                BeanPropertyBindingResult res = new BeanPropertyBindingResult(transactionDto, "somename");
                validator.validate(transactionDto, res);
                return getValidationErrorDto(res);
            }
        }).get();
        if (dto != null){
            return getValidationErrorDtoResponseEntity(dto);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validateWithArgCache")
    @ResponseBody
    public ResponseEntity<Object> validateWithArgCache(@RequestBody TransactionDto transaction) throws ExecutionException, InterruptedException {
        ValidationErrorDto dto = resultCache.compute(transaction, transactionDto -> {
            log.info("cacheable function for argument cache validating " + transactionDto);
            BeanPropertyBindingResult res = new BeanPropertyBindingResult(transactionDto, "somename");
            validator.validate(transactionDto, res);
            return getValidationErrorDto(res);
        }).get();
        if (dto != null){
            return getValidationErrorDtoResponseEntity(dto);
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handle(MethodArgumentNotValidException e) {
        BindingResult br = e.getBindingResult();
        ValidationErrorDto result = getValidationErrorDto(br);
        return getValidationErrorDtoResponseEntity(result);
    }

    private ResponseEntity<Object> getValidationErrorDtoResponseEntity(ValidationErrorDto result) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    private ValidationErrorDto getValidationErrorDto(BindingResult br) {
        List<String> globalErrors = br.getGlobalErrors().stream().map(ge -> ge.getDefaultMessage()).collect(Collectors.toList());
        Map<String, List<String>> fieldErrors = br.getFieldErrors().stream()
                .collect(Collectors.groupingBy(oe -> oe.getField(),
                        Collectors.mapping(oe -> oe.getDefaultMessage(), Collectors.toList())));
        return ValidationErrorDto.builder()
                .globalErrors(globalErrors)
                .fieldErrors(fieldErrors)
                .build();
    }
}
