package com.goorm.tricountapi.service;

import com.goorm.tricountapi.dto.ExpenseRequest;
import com.goorm.tricountapi.dto.ExpenseResult;
import com.goorm.tricountapi.enums.TricountApiErrorCode;
import com.goorm.tricountapi.model.Expense;
import com.goorm.tricountapi.model.Member;
import com.goorm.tricountapi.model.Settlement;
import com.goorm.tricountapi.repository.ExpenseRepository;
import com.goorm.tricountapi.repository.MemberRepository;
import com.goorm.tricountapi.repository.SettlementRepository;
import com.goorm.tricountapi.util.TricountApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public ExpenseResult addExpense(ExpenseRequest expenseRequest) {
        // 예외 처리
        // 없는 멤버 id로 요청을 보낸 경우
        Optional<Member> payer = memberRepository.findById(expenseRequest.getPayerMemberId());
        if(!payer.isPresent()) {
            throw new TricountApiException("INVALID MEMBER(Payer) ID! ", TricountApiErrorCode.INVALID_INPUT_VALUE);
        }

        // 없는 정산 id로 요청을 보낸 경우
        Optional<Settlement> settlement = settlementRepository.findById(expenseRequest.getSettlementId());
        if(!settlement.isPresent()) {
            throw new TricountApiException("INVALID Settlement ID!", TricountApiErrorCode.INVALID_INPUT_VALUE);
        }

        // 지출 추가
        Expense expense = Expense.builder()
                .name(expenseRequest.getName())
                .settlementId(expenseRequest.getSettlementId())
                .payerMemberId(expenseRequest.getPayerMemberId())
                .amount(expenseRequest.getAmount())
                // 요청이 들어오면 그 값을 넣고, 안들어오면 현재 시간을 기본적으로 넣어준다.
                .expenseDateTime(Objects.nonNull(expenseRequest.getExpenseDateTime()) ? expenseRequest.getExpenseDateTime() : LocalDateTime.now() )
                .build();

        expenseRepository.save(expense);

        return new ExpenseResult(expense, payer.get());
    }

}
