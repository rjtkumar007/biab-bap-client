package org.beckn.one.sandbox.bap.client.factories

import org.litote.kmongo.Id
import org.litote.kmongo.newId
import org.springframework.stereotype.Service

@Service
class DbIdFactory private constructor() {
  fun createStringId(): Id<String> {
    return newId()
  }

  companion object {
    private var instance: DbIdFactory? = null

    fun instance(): DbIdFactory {
      if (instance == null) {
        instance = DbIdFactory()
      }
      return instance!!
    }

    fun setInstance(factory: DbIdFactory) {
      instance = factory
    }
  }
}
