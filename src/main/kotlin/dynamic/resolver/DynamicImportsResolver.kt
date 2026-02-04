package dynamic.resolver

import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyImportElement
import com.jetbrains.python.psi.PyQualifiedExpression
import com.jetbrains.python.psi.resolve.PyReferenceResolveProvider
import com.jetbrains.python.psi.resolve.RatedResolveResult
import com.jetbrains.python.psi.types.TypeEvalContext

class DynamicImportsResolver : PyReferenceResolveProvider {

    override fun resolveName(
        element: PyQualifiedExpression,
        context: TypeEvalContext
    ): List<RatedResolveResult?> {

        val pare = element.parent

        return emptyList()

        val parent = element.parent as? PyImportElement ?: return emptyList()
        if (parent.name != "models") return emptyList()

        val scope = GlobalSearchScope.projectScope(element.project)

        val files = FilenameIndex.getFilesByName(element.project, "${element.name}.py", scope)

        val results = mutableListOf<RatedResolveResult?>()

        for (file in files) {
            if (file is PyFile) {
                file.topLevelClasses
                    .firstOrNull { it.name == element.name }
                    ?.let { results.add(RatedResolveResult(1, it)) }
            }
        }

        return results
    }

}