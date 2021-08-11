package org.beckn.one.sandbox.bap.extensions

import arrow.core.Either

fun <L, R> Either<L, R>.orElse(other: () -> Either<L, R>): Either<L, R> = fold({ other() }, { this })
