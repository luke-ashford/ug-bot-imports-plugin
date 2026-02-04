package dynamic.resolver

import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.impl.PyImportResolver
import com.jetbrains.python.psi.resolve.PyQualifiedNameResolveContext

class TestImportsResolver : PyImportResolver{

    override fun resolveImportReference(
        name: QualifiedName,
        context: PyQualifiedNameResolveContext,
        withRoots: Boolean
    ): PsiElement? {

        if (name.firstComponent == "models" && name.componentCount == 1) {
            return null

        }

        return null


    }
}