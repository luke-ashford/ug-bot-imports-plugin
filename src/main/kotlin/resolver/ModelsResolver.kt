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
        /* When "models" package is referenced, this extension will collect and return the classes within it
           as members of the package, where PyCharm IDE by default returns the modules instead. In this pattern,
           __init__.py collects the classes within the Python files found within models/, where the classes are
           of the same name as the file, and append them to __all__ such that they effectively belong to
           "models", rather than their original files. This extension resolves references that are otherwise
           lost by this dynamic import pattern.
        */
        if (memberName != "models") return emptyList() // Ignore if the reference is anything but models

        val virtualFile = file.virtualFile ?: return emptyList()
        if (virtualFile.name != "__init__.py") return emptyList()

        val dir = virtualFile.parent ?: return emptyList() // models/ directory

        val memberClasses = dir.children
            .asSequence()
            .filter { it.name != "__init__.py" && it.name.endsWith(".py") } // Find all Python files in models/ dir
            .mapNotNull { dir.findChild(it.name) }
            .mapNotNull { PsiManager.getInstance(file.project).findFile(it) as? PyFile } // Load them as PyFile PSI Elements
            .filter { it.parent?.name == "models" }
            .flatMap { it.topLevelClasses.asSequence() } // Get classes (PyClass PSI Element) from PyFiles
            .map { createResolvedMember(it.name!!, it) } // Generate the reference object for each class
            .toList()

        return memberClasses // Return the list of references for classes to models/ as members of models/
    }

    private fun createResolvedMember(name: String, target: PsiElement): PyCustomMember? {
        return PyCustomMember(name, target)
    }
}