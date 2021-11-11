package org.beckn.one.sandbox.bap.client.accounts.billings.services

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.BillingDetailsDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus

internal class BillingDetailServiceTest : DescribeSpec(){

  private val responseStorageService = mock<ResponseStorageService<BillingDetailsResponse, BillingDetailsDao>> {
    onGeneric { findManyByUserId(any(), any(), any()) }.thenReturn(Either.Right(ArrayList<BillingDetailsResponse>()))
  }

  private val responseStorageServiceNoData = mock<ResponseStorageService<BillingDetailsResponse, BillingDetailsDao>> {
    onGeneric { findManyByUserId(any(), any(), any()) }.thenReturn(Either.Left(DatabaseError.NoDataFound))
  }

  init {
    describe("BillingDetailsService test"){
      it("Should return Database NoData error"){
        var billingDetailService = BillingDetailService(
          responseStorageService = responseStorageServiceNoData
        )
        billingDetailService.findBillingsForCurrentUser("test").statusCode shouldBe DatabaseError.NoDataFound.status()
      }
      it("Should return empty billing details list with status code OK"){
        var billingDetailService = BillingDetailService(
          responseStorageService = responseStorageService
        )
        billingDetailService.findBillingsForCurrentUser("test").statusCode shouldBe HttpStatus.OK
      }
    }
  }

}