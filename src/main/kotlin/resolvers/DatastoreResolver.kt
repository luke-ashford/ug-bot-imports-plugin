package resolvers

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.jetbrains.python.codeInsight.PyCustomMember
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.resolve.PyResolveContext
import com.jetbrains.python.psi.types.PyClassMembersProvider
import com.jetbrains.python.psi.types.PyClassType
import com.jetbrains.python.psi.types.TypeEvalContext

class DatastoreResolver : PyClassMembersProvider {

    override fun getMembers(
        clazz: PyClassType?,
        location: PsiElement?,
        context: TypeEvalContext
    ): Collection<PyCustomMember?> {

        if (clazz?.name != "Roblox") return emptyList()

        val dir = clazz.pyClass.parent.parent as? PsiDirectory ?: return emptyList()

        val memberClasses = getUniverseClasses(dir)
             .map { PyCustomMember(it.name!!, it) }

        return memberClasses

    }

    override fun resolveMember(
        type: PyClassType,
        name: String,
        location: PsiElement?,
        resolveContext: PyResolveContext
    ): PsiElement? {

        if (type.pyClass.name != "Roblox") return null

        val dir = type.pyClass.parent.parent as? PsiDirectory ?: return null

        val clazz = getUniverseClasses(dir)
            .firstOrNull { it.name!! == name }

        return clazz

    }

    private fun getUniverseClasses(dir: PsiDirectory): List<PyClass> {
        val universeDir = dir.children
            .filterIsInstance<PsiDirectory>()
            .firstOrNull { it.name == "Universes" }
            ?: return emptyList()

        return universeDir.children
            .filterIsInstance<PyFile>()
            .flatMap { it.topLevelClasses.asSequence() }
    }

}