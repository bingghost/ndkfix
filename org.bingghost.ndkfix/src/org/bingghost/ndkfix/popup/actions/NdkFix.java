package org.bingghost.ndkfix.popup.actions;

import java.io.File;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;


@SuppressWarnings("restriction")
public class NdkFix implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public NdkFix() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		String path = getProjectPath();
		IProject myProject = getCurrentProject();
		path = path + myProject.getFullPath();
		
		fixNdkBugs(path);
		
		MessageDialog.openInformation(shell, "ndkfix", "请重启Eclipse->刷新工程->重新Add Ndk Native");

	}
	
	public void fixNdkBugs(String path) {
		String project_path = path + "/.project"; 
		String cproject_path = path + "/.cproject";
		
		XmlOprate xmlOprate = new XmlOprate(project_path);
		xmlOprate.fixProjectConfig();
		
		deleteFile(cproject_path);
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	public void deleteFile(String sPath) {   
		File file = new File(sPath);  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
	}
	
	

	public String getProjectPath() {
		String path = null;
		path = Platform.getLocation().toString();
		return path;
	}

	public IProject getCurrentProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();

		ISelection selection = selectionService.getSelection();

		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
			} else if (element instanceof PackageFragmentRootContainer) {
				IJavaProject jProject = ((PackageFragmentRootContainer) element).getJavaProject();
				project = jProject.getProject();
			} else if (element instanceof IJavaElement) {
				IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				project = jProject.getProject();
			} else if (element instanceof ICElement) {
				ICProject cProject = ((ICElement) element).getCProject();
				project = cProject.getProject();
			}

		}
		return project;
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
