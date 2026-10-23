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

        val cwdir = clazz?.pyClass?.parent?.parent as? PsiDirectory ?: return emptyList()
        if (cwdir.name != "Universes") return emptyList()

        val glob_dir = cwdir.parent as PsiDirectory
        val datastore_dir = glob_dir.findSubdirectory("Datastores") ?: return emptyList()
        val universe_dir = datastore_dir.findSubdirectory(clazz.name!!) ?: return emptyList()

        val memberClasses = getDatastoreClasses(universe_dir)
             .map { PyCustomMember(it.name!!, it) }

        return memberClasses
    }

    override fun resolveMember(
        type: PyClassType,
        name: String,
        location: PsiElement?,
        resolveContext: PyResolveContext
    ): PsiElement? {

        val cwdir = type.pyClass.parent.parent as? PsiDirectory ?: return null
        if (cwdir.name != "Universes") return null

        val glob_dir = cwdir.parent as PsiDirectory
        val datastore_dir = glob_dir.findSubdirectory("Datastores") ?: return null
        val universe_dir = datastore_dir.findSubdirectory(type.name!!) ?: return null

        val clazz = getDatastoreClasses(universe_dir)
            .firstOrNull { it.name!! == name }

        return clazz

    }

    private fun getDatastoreClasses(dir: PsiDirectory): List<PyClass> {
        return dir.children
            .filterIsInstance<PyFile>()
            .flatMap { it.topLevelClasses.asSequence() }
    }

}