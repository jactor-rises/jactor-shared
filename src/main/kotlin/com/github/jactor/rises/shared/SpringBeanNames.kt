package com.github.jactor.rises.shared

@JvmRecord
data class SpringBeanNames(
    private val beanFilter: BeanFilter = BeanFilter.PACKAGE_LEVEL_4,
    private val beanNames: BeanNames = BeanNames(),
    private val externalBeanNames: BeanNames = BeanNames(),
    private val numberOfBeansOnLine: Int = NO_OF_BEANS_ON_LINE,
    private val projectPackage: String = "com.github.jactor.rises",
    private val splitExternalNames: Boolean = false,
) {
    companion object {
        const val NO_OF_BEANS_ON_LINE = 4
    }

    val names: List<String> get() = beanNames.names
    val externalNames: List<String> get() = externalBeanNames.names

    fun add(name: String) {
        shouldAddName(
            beanPackage = name.substringBeforeLast(delimiter = '.'),
        ).let { (packageName, shouldAdd) ->
            val simpleName = name.substringAfterLast(delimiter = '.')

            when (splitExternalNames) {
                false -> shouldAdd.whenTrue {
                    beanNames.add(
                        name = simpleName,
                        numberOfBeansOnLine = numberOfBeansOnLine,
                    )
                }

                true -> when (packageName.contains(projectPackage)) {
                    true -> beanNames.add(
                        name = simpleName,
                        numberOfBeansOnLine = numberOfBeansOnLine,
                    )

                    false -> externalBeanNames.add(
                        name = simpleName,
                        numberOfBeansOnLine = numberOfBeansOnLine,
                    )
                }
            }
        }
    }

    private fun shouldAddName(beanPackage: String): AddName = when (beanFilter) {
        BeanFilter.NONE -> AddName(packageName = beanPackage, shouldAdd = true)
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

            AddName(packageName = beanPackage, shouldAdd = beanPackageLimited == callerPackageLimited)
        }
    }
}

private data class AddName(val packageName: String, val shouldAdd: Boolean)

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
