package com.api.GestionaFacilRestaurants.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse {
    private boolean success=true;
    @JsonInclude(Include.NON_NULL)
    private Object data;
    @JsonInclude(Include.NON_NULL)
    private String message;
    @JsonInclude(Include.NON_NULL)
    private Long total_registers;
    private String updated_token;
    public SuccessResponse(Object data,String updated_token,Long total_registers,String message) {
        this.data = data;
        this.updated_token = updated_token;
        this.total_registers = total_registers;
        this.message = message;
    }
    public SuccessResponse(Object data,String updated_token){
        this(data,updated_token,null,null);
    }

    // Constructor con data, updated_token y message (sin total_registers)
    public SuccessResponse(Object data, String updated_token, String message) {
        this(data, updated_token, null, message);
    }

    // Constructor con data, updated_token y total_registers (sin message)
    public SuccessResponse(Object data, String updated_token, Long total_registers) {
        this(data, updated_token, total_registers, null);
    }
}
