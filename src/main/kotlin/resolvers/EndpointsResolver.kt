package resolvers

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.jetbrains.python.codeInsight.PyCustomMember
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.resolve.PyResolveContext
import com.jetbrains.python.psi.types.PyClassMembersProvider
import com.jetbrains.python.psi.types.PyClassType
import com.jetbrains.python.psi.types.TypeEvalContext
import kotlin.collections.firstOrNull

class EndpointsResolver : PyClassMembersProvider {

    override fun getMembers(
        clazz: PyClassType?,
        location: PsiElement?,
        context: TypeEvalContext
    ): Collection<PyCustomMember?> {

        val dir = clazz?.pyClass?.parent?.parent as? PsiDirectory ?: return emptyList()
        if (dir.name != "Endpoints") return emptyList()

        val memberClasses = getEndpointFuncs(dir, clazz.name!!)
            .map { PyCustomMember(resolveName(it.name!!, clazz?.name!!), it) }

        return memberClasses

    }

    override fun resolveMember(
        type: PyClassType,
        name: String,
        location: PsiElement?,
        resolveContext: PyResolveContext
    ): PsiElement? {

        val dir = type.pyClass.parent.parent as? PsiDirectory ?: return null
        if (dir.name != "Endpoints") return null

        val clazz = getEndpointFuncs(dir, type.pyClass.name!!)
            .firstOrNull { resolveName(it.name!!, type.pyClass.name!!) == name }

        return clazz

    }

    private fun getEndpointFuncs(cwdir: PsiDirectory, folder: String): List<PyFunction> {
        return cwdir
            .children
            .filterIsInstance<PsiDirectory>()
            .firstOrNull() { it.name == folder }
            ?.children
            ?.filterIsInstance<PyFile>()
            ?.flatMap { it.children.filterIsInstance<PyFunction>() }
            ?: emptyList()
    }

    private fun resolveName(name: String, folder: String): String {
        return name.removePrefix("${folder.lowercase()}_")
    }

}