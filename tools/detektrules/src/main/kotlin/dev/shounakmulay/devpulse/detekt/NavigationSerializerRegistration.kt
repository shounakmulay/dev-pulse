package dev.shounakmulay.devpulse.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import java.io.File

class NavigationSerializerRegistration(config: Config) : Rule(config) {
    override val issue = Issue(
        id = "NavigationSerializerRegistration",
        severity = Severity.Defect,
        description = "Back-stack routes must be registered in the NavKey serializers module.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (File(file.name).name != "NavigationState.kt" || file.packageFqName.asString() != NAVIGATION_PACKAGE) return
        val routeFile = File(file.virtualFilePath).parentFile.resolve("Screen.kt")
        val property = file.declarations.filterIsInstance<KtProperty>()
            .singleOrNull { it.name == "navigationSerializersConfig" }
        if (!routeFile.isFile || property == null) {
            report(CodeSmell(issue, Entity.from(file), "Cannot verify navigation serializer registrations: Screen.kt or navigationSerializersConfig is missing."))
            return
        }
        val routes = KtPsiFactory(file.project, false).createFile(routeFile.readText())
        missingRoutes(routes, property).forEach { route ->
            report(CodeSmell(issue, Entity.from(property), "Register $route in navigationSerializersConfig's polymorphic NavKey module."))
        }
    }
}

internal fun missingRoutes(routes: KtFile, config: KtProperty): List<String> {
    val screen = routes.declarations.filterIsInstance<KtClassOrObject>().singleOrNull { it.name == "Screen" }
        ?: return listOf("Screen (route declarations not found)")
    val expected = mutableListOf<String>()
    fun collect(parent: KtClassOrObject, path: String) {
        parent.declarations.filterIsInstance<KtClassOrObject>().forEach { declaration ->
            val name = declaration.name ?: return@forEach
            val qualified = "$path.$name"
            if (declaration.superTypeListEntries.any { it.typeReference?.text == "Screen" } &&
                (declaration !is KtClass || (!declaration.isInterface() && !declaration.isSealed() && !declaration.hasModifier(org.jetbrains.kotlin.lexer.KtTokens.ABSTRACT_KEYWORD)))
            ) {
                expected.add(qualified)
            }
            collect(declaration, qualified)
        }
    }
    collect(screen, "Screen")
    val registered = mutableSetOf<String>()
    config.accept(object : KtTreeVisitorVoid() {
        override fun visitCallExpression(expression: KtCallExpression) {
            if (expression.calleeExpression?.text == "polymorphic" &&
                expression.valueArguments.firstOrNull()?.getArgumentExpression()?.text == "NavKey::class"
            ) {
                expression.lambdaArguments.forEach { lambda ->
                    lambda.accept(object : KtTreeVisitorVoid() {
                        override fun visitCallExpression(call: KtCallExpression) {
                            if (call.calleeExpression?.text == "subclass") {
                                val args = call.valueArguments.map { it.getArgumentExpression()?.text?.replace(Regex("\\s+"), "") }
                                val route = args.firstOrNull()?.removeSuffix("::class")
                                if (route != null && args.size >= 2 && args[0] == "$route::class" && args[1] == "$route.serializer()") registered.add(route)
                            }
                            super.visitCallExpression(call)
                        }
                    })
                }
            }
            super.visitCallExpression(expression)
        }
    })
    return expected.filterNot(registered::contains)
}

private const val NAVIGATION_PACKAGE = "dev.shounakmulay.devpulse.core.navigation"
