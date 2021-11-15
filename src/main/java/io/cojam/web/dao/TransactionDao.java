package io.cojam.web.dao;

import io.cojam.web.domain.wallet.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TransactionDao {

    int saveTransaction(Transaction transaction);

    List<Transaction> getTransactionList(Transaction transaction);

    Integer getTransactionListCnt(Transaction transaction);

    Transaction getTransaction(String transactionKey);

    int updateTransactionStatus(Transaction transaction);


    List<Transaction> getTransactionStatusCheckList(Transaction transaction);

    int updateTransactionStatusCheck(Transaction transaction);

}
