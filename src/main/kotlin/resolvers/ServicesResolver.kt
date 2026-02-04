package resolvers

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.jetbrains.python.codeInsight.PyCustomMember
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.types.PyModuleMembersProvider
import com.jetbrains.python.psi.types.TypeEvalContext

class ServicesResolver : PyModuleMembersProvider() {

    override fun getMembersByQName(
        file: PyFile,
        memberName: String,
        context: TypeEvalContext
    ): Collection<PyCustomMember?> {

        if (memberName != "services") return emptyList()

        val virtualFile = file.virtualFile ?: return emptyList()
        if (virtualFile.name != "__init__.py") return emptyList()

        val dir = virtualFile.parent ?: return emptyList()

        val memberClasses = dir.children
            .asSequence()
            .filter { it.name != "__init__.py" && it.name != "__pycache__" }
            .mapNotNull { dir.findChild(it.name) }
            .mapNotNull { it.findChild( it.name.replaceFirstChar { it.uppercaseChar() } + ".py" ) }
            .mapNotNull { PsiManager.getInstance(file.project).findFile(it) as? PyFile }
            .filter { it.parent?.parent?.name == "services" }
            .flatMap { it.topLevelClasses.asSequence() }
            .map { createResolvedMember(it.name!!.lowercase(), it) }
            .toList()

        return memberClasses
    }

    private fun createResolvedMember(name: String, target: PsiElement): PyCustomMember? {
        return PyCustomMember(name, target)
    }

}