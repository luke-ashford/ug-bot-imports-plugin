package resolver

import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.python.codeInsight.PyCustomMember
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.types.PyModuleMembersProvider
import com.jetbrains.python.psi.types.TypeEvalContext
import com.intellij.psi.PsiElement

class ModelsResolver : PyModuleMembersProvider() {

    override fun getMembersByQName(
        file: PyFile,
        memberName: String,
        context: TypeEvalContext
    ): Collection<PyCustomMember?> {

        if (memberName != "models") return emptyList()

        val virtualFile = file.virtualFile ?: return emptyList()
        if (virtualFile.name != "__init__.py") return emptyList()

        val dir = virtualFile.parent ?: return emptyList()

        val scope = GlobalSearchScope.projectScope(file.project)

        val members = dir.children
            .filter { it.name != "__init__.py" && it.name.endsWith(".py") }
            .map { it.name }
            .flatMap { FilenameIndex.getFilesByName(file.project, it, scope).asSequence() }
            .filterIsInstance<PyFile>()
            .filter { it.parent?.name == "models" }
            .flatMap { it.topLevelClasses.asSequence() }
            .map { createResolvedMember(it.name!!, it) }
            .toList()

        return members
    }

    private fun createResolvedMember(name: String, target: PsiElement): PyCustomMember? {
        return PyCustomMember(name, target)
    }
}