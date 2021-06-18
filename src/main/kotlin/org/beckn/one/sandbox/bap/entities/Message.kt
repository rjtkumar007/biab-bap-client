package org.beckn.one.sandbox.bap.entities

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class Message(
  @field:BsonId val objectId: Id<String> = newId(),
  @field:BsonId val id: String
)