package resolver

import com.jetbrains.python.codeInsight.PyCustomMember
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.types.PyModuleMembersProvider
import com.jetbrains.python.psi.types.TypeEvalContext
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager

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

        val memberClasses = dir.children
            .asSequence()
            .filter { it.name != "__init__.py" && it.name.endsWith(".py") }
            .mapNotNull { dir.findChild(it.name) }
            .mapNotNull { PsiManager.getInstance(file.project).findFile(it) as? PyFile}
            .filter { it.parent?.name == "models" }
            .flatMap { it.topLevelClasses.asSequence() }
            .map { createResolvedMember(it.name!!, it) }
            .toList()

        return memberClasses
    }

    private fun createResolvedMember(name: String, target: PsiElement): PyCustomMember? {
        return PyCustomMember(name, target)
    }
}