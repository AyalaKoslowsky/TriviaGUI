package il.ac.tau.cs.sw1.trivia;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GUIUtils {

	/**
	 * open a dialog with information to the user.
	 * 
	 * @param shell
	 * @param title
	 *            of the dialog
	 * @param message
	 *            to display
	 */
	public static void showInfoDialog(Shell shell, String title,
			String message) {
		showDialog(shell, title, message, SWT.ICON_INFORMATION);
	}

	/**
	 * open a dialog with an error message to the user.
	 * 
	 * @param shell
	 * @param message
	 *            to display
	 */
	public static void showErrorDialog(Shell shell, String message) {
		showDialog(shell, "Error", message, SWT.ICON_ERROR);
	}

	/**
	 * Opens a message dialog
	 * 
	 * @param shell
	 *            - the parent window
	 * @param title
	 *            - the dialog title
	 * @param message
	 *            - the message to display
	 * @param icon
	 *            - the type of SWT icon to display
	 */
	private static void showDialog(Shell shell, String title,
			String message, int icon) {
		MessageBox messageBox = new MessageBox(shell, icon | SWT.OK);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}
	
	/**
	 * Opens a file selection dialog
	 * @param shell
	 * @return the path of the selected file, or null if "Cancel" was pressed.
	 */
	public static String getFilePathFromFileDialog(Shell shell) {
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);

		String path = fileDialog.open();
		return path;
	}

	/**
	 * @return A GridData element that fills the horizontal space of the row, spanning over the input number of columns
	 */
	public static GridData createFillGridData(int span) {
		GridData toReturn  = new GridData(GridData.FILL, GridData.CENTER, true,
				false);
		toReturn.horizontalSpan = span;
		return toReturn;
	}
}
