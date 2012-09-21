package plug.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.console.*;
import java.io.*; 



/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SyncOutHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SyncOutHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorInput editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		final IFile file = (IFile)editor.getAdapter(IFile.class);
		Job sync_out = new Job("Fishbowl Syn out"){
			public IStatus run(IProgressMonitor monitor) {
				if(file != null)
				{	
					String filename = file.getName().toString();
					System.out.println(filename);
					try 
					{ 
						String command = "/opt/vde/services/instances/vde_latest/bin/vde_tool ";
						String flag    = "--Vforce --Vtime 30d --Vreason bugfix sync_out ";
						//String command = "cmd ";
						//String flag    = "/c ";
						File path =  new File(file.getParent().getLocation().toString());
						System.out.println(path.toString());
						Process pro = Runtime.getRuntime().exec("/bin/rm " + filename, null, path);     
						pro = Runtime.getRuntime().exec(command + flag + filename, null, path);          
						//pro.waitFor(); 
						InputStream out = pro.getInputStream(); 
						InputStream err = pro.getErrorStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(out));
						String msg = null; 
						String all_msg = "";
						while((msg = br.readLine())!= null){ 
							all_msg = all_msg + msg + "\n\r";
						}
						br =  new BufferedReader(new InputStreamReader(err));
						while((msg = br.readLine())!= null){ 
							all_msg = all_msg + msg + "\n\r";
						}
						MessageConsole console = new MessageConsole("My Console", null);						 
						console.clearConsole();
						console.activate();					 
						ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
						MessageConsoleStream consoleStream = console.newMessageStream();
						consoleStream.println(all_msg);
				        Display.getDefault().asyncExec(new Runnable() {                        
				            public void run() {                                                                                   
								MessageDialog.openInformation(
										null,
										"Fishbowl",
										"Syn out OK");
				            }
				        });	
					} 
					catch(IOException exception1) 
					{ 
						System.out.println("error"); 
					} 
//					catch(InterruptedException exception2) 
//					{ 
//						System.out.println("error"); 
//					} 
				}    		
				return  Status.OK_STATUS;
			}
		};
		sync_out.schedule(); 
		return null;
	}
}
