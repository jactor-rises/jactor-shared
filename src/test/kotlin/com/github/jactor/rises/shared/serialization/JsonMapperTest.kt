package com.github.jactor.rises.shared.serialization

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.jactor.rises.shared.api.BlogDto
import com.github.jactor.rises.shared.api.BlogEntryDto
import com.github.jactor.rises.shared.api.CreateBlogEntryCommand
import com.github.jactor.rises.shared.api.CreateGuestBookCommand
import com.github.jactor.rises.shared.api.CreateGuestBookEntryCommand
import com.github.jactor.rises.shared.api.CreateUserCommand
import com.github.jactor.rises.shared.api.GuestBookDto
import com.github.jactor.rises.shared.api.GuestBookEntryDto
import com.github.jactor.rises.shared.api.PersistentDto
import com.github.jactor.rises.shared.api.PersonDto
import com.github.jactor.rises.shared.api.UpdateBlogTitleCommand
import com.github.jactor.rises.shared.api.UserDto
import com.github.jactor.rises.shared.test.roundDownToMinute
import org.junit.jupiter.api.Named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.UUID

class JsonMapperTest {
    @ParameterizedTest
    @MethodSource("initDataForSerialization")
    fun `should serialize and read from json`(
        any: Any,
        clazz: Class<*>,
    ) {
        val json = JsonMapper.objectMapper.writeValueAsString(any)
        val read = JsonMapper.objectMapper.readValue(json, clazz)

        assertThat(read).isEqualTo(any)
    }

    companion object {
        @JvmStatic
        fun initDataForSerialization(): List<Arguments> =
            listOf(
                CreateUserCommand(
                    addressId = UUID.randomUUID(),
                    personId = UUID.randomUUID(),
                    username = "burn",
                    surname = "stakehouse",
                    emailAddress = "somewhere@ourt.there.com",
                    description = "it is cold outside",
                    firstName = "kevin",
                    language = "nb",
                    addressLine1 = "one street",
                    addressLine2 = "on the corner",
                    addressLine3 = "by the way",
                    zipCode = "12345",
                    city = "the big one",
                    country = "no",
                ) to CreateUserCommand::class.java,
                CreateBlogEntryCommand(
                    blogId = UUID.randomUUID(),
                    creatorName = "me",
                    entry = "life is good",
                ) to CreateBlogEntryCommand::class.java,
                CreateGuestBookCommand(
                    title = "once upon a time",
                    userId = UUID.randomUUID(),
                ) to CreateGuestBookCommand::class.java,
                CreateGuestBookEntryCommand(
                    guestBookId = UUID.randomUUID(),
                    creatorName = "visitor",
                    entry = "nice place you have here",
                ) to CreateGuestBookEntryCommand::class.java,
                UpdateBlogTitleCommand(
                    blogId = UUID.randomUUID(),
                    title = "a new dawn",
                ) to UpdateBlogTitleCommand::class.java,
                BlogDto(
                    persistentDto = initPersistentDto(),
                    title = "my blog",
                    userId = UUID.randomUUID(),
                ) to BlogDto::class.java,
                BlogEntryDto(
                    persistentDto = initPersistentDto(),
                    blogId = UUID.randomUUID(),
                    creatorName = "author",
                ) to BlogEntryDto::class.java,
                GuestBookDto(
                    persistentDto = initPersistentDto(),
                    title = "the guest book",
                    userId = UUID.randomUUID(),
                ) to GuestBookDto::class.java,
                GuestBookEntryDto(
                    persistentDto = initPersistentDto(),
                    creatorName = "guest",
                    entry = "hello world",
                    guestBookId = UUID.randomUUID(),
                ) to GuestBookEntryDto::class.java,
                UserDto(
                    persistentDto = initPersistentDto(),
                    emailAddress = "somewhat@strange.com",
                    personId = UUID.randomUUID(),
                    username = "strangeuser",
                    userType = com.github.jactor.rises.shared.api.UserType.ACTIVE,
                ) to UserDto::class.java,
                PersonDto(
                    persistentDto = initPersistentDto(),
                    addressId = UUID.randomUUID(),
                    locale = "en",
                    firstName = "first",
                    surname = "last",
                    description = "just a person",
                ) to PersonDto::class.java,
            ).map { Arguments.of(Named.of(it.first::class.simpleName!!, it.first), it.second) }
        private fun initPersistentDto(): PersistentDto =
            PersistentDto(
                id = UUID.randomUUID(),
                createdBy = "creator",
                modifiedBy = "modifier",
                timeOfCreation = LocalDateTime.now().roundDownToMinute(),
                timeOfModification = LocalDateTime.now().roundDownToMinute(),
            )
    }
}
