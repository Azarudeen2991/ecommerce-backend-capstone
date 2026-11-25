package org.example.evaluations.evaluation.controllers;

import com.razorpay.Refund;
import org.example.evaluations.evaluation.dtos.RefundRequestDto;
import org.example.evaluations.evaluation.dtos.RefundSpeed;
import org.example.evaluations.evaluation.services.IRefundService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RefundController {

    @Autowired
    private IRefundService refundService;

    //Add your APIs here
    @PostMapping("/issueRefund")
    public String issueRefund(@RequestBody RefundRequestDto refundRequestDto) {
        return refundService.issueRefund(refundRequestDto.getAmount(),
                refundRequestDto.getReceipt());
    }

    @PatchMapping("/updateRefund/{refundId}")
    public String updateRefund(@PathVariable String refundId, @RequestBody RefundRequestDto refundRequestDto) {

        JSONObject refundDetails = new JSONObject();

        // validate each parameter before adding to the refundDetails JSON object
        if (refundRequestDto.getAmount() != null) {
            refundDetails.put("amount", refundRequestDto.getAmount());
        }
        if (refundRequestDto.getReceipt() != null) {
            refundDetails.put("receipt", refundRequestDto.getReceipt());
        }
        if (refundRequestDto.getRefundSpeed() != null) {
            if (refundRequestDto.getRefundSpeed().equals(RefundSpeed.optimum))
                refundDetails.put("speed", "optimum");
            else if (refundRequestDto.getRefundSpeed().equals(RefundSpeed.normal))
                refundDetails.put("speed", "normal");
        }

        return refundService.updateRefund(refundId, refundDetails);
    }
}
