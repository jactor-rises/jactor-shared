package com.github.jactor.rises.shared

@JvmRecord
data class SpringBeanNames(
    private val beanFilter: BeanFilter = BeanFilter.PACKAGE_LEVEL_4,
    private val beanNames: BeanNames = BeanNames(),
    private val externalBeanNames: BeanNames = BeanNames(),
    private val jactorPackage: String = "com.github.jactor.rises",
    private val numberOfBeansOnLine: Int = NO_OF_BEANS_ON_LINE,
    private val splitExternalNames: Boolean = false,
) {
    companion object {
        const val NO_OF_BEANS_ON_LINE = 4
    }

    val names: List<String> get() = beanNames.names
    val externalNames: List<String> get() = externalBeanNames.names

    fun add(name: String) {
        @Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        addingName(
            beanPackage = name.substringBeforeLast(delimiter = '.'),
        ).let {
            when (splitExternalNames) {
                false -> it.whenTrue {
                    beanNames.add(
                        name = name.substringAfterLast(delimiter = '.'),
                        numberOfBeansOnLine = numberOfBeansOnLine,
                    )
                }

                true -> {
                    it.whenTrue {
                        beanNames.add(
                            name = name.substringAfterLast(delimiter = '.'),
                            numberOfBeansOnLine = numberOfBeansOnLine,
                        )
                    } ?: externalBeanNames.add(
                        name = name.substringAfterLast(delimiter = '.'),
                        numberOfBeansOnLine = numberOfBeansOnLine,
                    )
                }
            }
        }
    }

    private fun addingName(beanPackage: String): Boolean = when (beanFilter) {
        BeanFilter.NONE -> true
        BeanFilter.PACKAGE_LEVEL_4 -> {
            val beanPackageLimited = beanPackage.split(delimiters = arrayOf("."), limit = 4)
            val callerPackage = StackWalker.getInstance()
                .walk { frames ->
                    frames.map { it.className }
                        .dropWhile { it == SpringBeanNames::class.qualifiedName }
                        .findFirst()
                        .map { it.substringBeforeLast(delimiter = '.') }
                        .orElse("package not found")
                }!!

            val callerPackageLimited = callerPackage.split(delimiters = arrayOf("."), limit = 4)

            beanPackageLimited == callerPackageLimited
        }
    }
}

enum class BeanFilter {
    NONE,
    PACKAGE_LEVEL_4,
}

@JvmRecord
data class BeanNames(
    private val beanNames: MutableList<String> = mutableListOf(),
    private val tenNames: MutableList<String> = mutableListOf(),
) {
    val names: List<String>
        get() = joinLists()

    private fun joinLists(): List<String> = tenNames.isNotEmpty().whenTrue {
        beanNames + tenNames.joinToString(separator = ", ")
    } ?: beanNames

    fun add(name: String, numberOfBeansOnLine: Int) {
        tenNames.add(name)

        if (tenNames.size == numberOfBeansOnLine) {
            beanNames.add(tenNames.joinToString(separator = ", "))
            tenNames.clear()
        }
    }
}
