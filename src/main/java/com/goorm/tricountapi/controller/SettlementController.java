package com.goorm.tricountapi.controller;

import com.goorm.tricountapi.dto.BalanceResult;
import com.goorm.tricountapi.dto.ExpenseResult;
import com.goorm.tricountapi.model.ApiResponse;
import com.goorm.tricountapi.model.Settlement;
import com.goorm.tricountapi.service.SettlementService;
import com.goorm.tricountapi.util.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    // 새로운 정산 생성
    @PostMapping("/settles/create")
    public ApiResponse<Settlement> createSettlement(@RequestParam String settlementName) {
        return new ApiResponse<Settlement>().ok(
                settlementService.createAndJoinSettlement(settlementName, MemberContext.getCurrentMember())
        );
    }

    // 정산 참여
    @PostMapping("/settles/{id}/join")
    public ApiResponse joinSettlement(@PathVariable("id") Long settlementId) {
        settlementService.joinSettlement(settlementId, MemberContext.getCurrentMember().getId());
        return new ApiResponse<>().ok();
    }

    // 정산 결과 계산
    @GetMapping("/settles/{id}/balance")
    public ApiResponse<BalanceResult> getSettlementBalanceResult(@PathVariable("id") Long settlementId) {
        return new ApiResponse<BalanceResult>().ok(settlementService.getBalanceResult(settlementId));
    }

    // 특정 정산에 속하는 지출 조회
    @GetMapping("/settles/{id}/expenses")
    public ApiResponse<ExpenseResult> getSettlementExpense(@PathVariable("id") Long settlementId ) {
        return new ApiResponse<ExpenseResult>().ok(settlementService.getSettlementExpenses(settlementId));
    }
}
