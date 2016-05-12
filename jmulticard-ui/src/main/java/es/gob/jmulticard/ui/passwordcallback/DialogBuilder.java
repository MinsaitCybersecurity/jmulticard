/*
 * Controlador Java de la Secretaria de Estado de Administraciones Publicas
 * para el DNI electronico.
 *
 * El Controlador Java para el DNI electronico es un proveedor de seguridad de JCA/JCE
 * que permite el acceso y uso del DNI electronico en aplicaciones Java de terceros
 * para la realizacion de procesos de autenticacion, firma electronica y validacion
 * de firma. Para ello, se implementan las funcionalidades KeyStore y Signature para
 * el acceso a los certificados y claves del DNI electronico, asi como la realizacion
 * de operaciones criptograficas de firma con el DNI electronico. El Controlador ha
 * sido disenado para su funcionamiento independiente del sistema operativo final.
 *
 * Copyright (C) 2012 Direccion General de Modernizacion Administrativa, Procedimientos
 * e Impulso de la Administracion Electronica
 *
 * Este programa es software libre y utiliza un licenciamiento dual (LGPL 2.1+
 * o EUPL 1.1+), lo cual significa que los usuarios podran elegir bajo cual de las
 * licencias desean utilizar el codigo fuente. Su eleccion debera reflejarse
 * en las aplicaciones que integren o distribuyan el Controlador, ya que determinara
 * su compatibilidad con otros componentes.
 *
 * El Controlador puede ser redistribuido y/o modificado bajo los terminos de la
 * Lesser GNU General Public License publicada por la Free Software Foundation,
 * tanto en la version 2.1 de la Licencia, o en una version posterior.
 *
 * El Controlador puede ser redistribuido y/o modificado bajo los terminos de la
 * European Union Public License publicada por la Comision Europea,
 * tanto en la version 1.1 de la Licencia, o en una version posterior.
 *
 * Deberia recibir una copia de la GNU Lesser General Public License, si aplica, junto
 * con este programa. Si no, consultelo en <http://www.gnu.org/licenses/>.
 *
 * Deberia recibir una copia de la European Union Public License, si aplica, junto
 * con este programa. Si no, consultelo en <http://joinup.ec.europa.eu/software/page/eupl>.
 *
 * Este programa es distribuido con la esperanza de que sea util, pero
 * SIN NINGUNA GARANTIA; incluso sin la garantia implicita de comercializacion
 * o idoneidad para un proposito particular.
 */
package es.gob.jmulticard.ui.passwordcallback;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.Console;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import es.gob.jmulticard.ui.passwordcallback.gui.CustomDialog;

/** Gestor de di&aacute;logos gr&aacute;ficos.
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s */
public final class DialogBuilder {

private static boolean headless = false;

    static void setHeadLess(final boolean hl) {
        headless = hl;
    }

    static {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                setHeadLess(Boolean.getBoolean("java.awt.headless")); //$NON-NLS-1$
                return null;
            }
        });
    }

    private DialogBuilder() {
        /* Constructor privado */
    }

    /** Muestra un di&aacute;logo para la confirmaci&oacute;n de una operaci&oacute;n con clave privada.
     * @param parent Componente padre para la modalidad.
     * @param digitalSignCert <code>true</code> si la operaci&oacute;n se hace con una clave destinada a
     *        firmas electr&oacute;nicas, <code>false</code> si la clave est&aacute;
     *        destinada a la autenticaci&oacute;n
     * @return <code>0</code> si el usuario acepta, <code>1</code> si rechaza hacer la operaci&oacute;n con
     *         clave privada */
    public static int showSignatureConfirmDialog(final Callback callBack) {
        if (!headless) {
            try {
                return CustomDialog.showConfirmDialog(
            		 PasswordCallbackManager.getDialogOwner(),
                     true,
                     ((ConfirmationCallback) callBack).getPrompt(),
	                 "Mensaje de seguridad",
	                 ((ConfirmationCallback) callBack).getMessageType()
                 );

            }
            catch (final java.awt.HeadlessException e) {
                Logger.getLogger("es.gob.jmulticard").info("No hay entorno grafico, se revierte a consola: " + e); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        final Console console = System.console();
        if (console == null) {
            throw new NoConsoleException("No hay consola para solicitar el PIN"); //$NON-NLS-1$
        }
        return getConsoleConfirm(console, callBack);
    }

    private static int getConsoleConfirm(final Console console, final Callback callBack) {
        console.printf(((ConfirmationCallback)callBack).getPrompt());
        final String confirm = console.readLine().replace("\n", "").replace("\r", "").trim().toLowerCase(Locale.getDefault()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        if ("si".equals(confirm) //$NON-NLS-1$
                || "s".equals(confirm) //$NON-NLS-1$
                || "s\u00ED".equals(confirm)) { //$NON-NLS-1$
                return 0;
        }
        else if ("no".equals(confirm) || "n".equals(confirm)) { //$NON-NLS-1$ //$NON-NLS-2$
        	return 1;
        } else {
			return getConsoleConfirm(console, callBack);
		}
    }

    public static String getCan(final Callback callBack) {
			final String CAN_EXAMPLE = "/images/can_example.png"; //$NON-NLS-1$
			final JLabel label1 = new JLabel(((TextInputCallback)callBack).getPrompt());
			final ImageIcon icon = new ImageIcon(DialogBuilder.class.getResource(CAN_EXAMPLE));
			final Image img = icon.getImage();
			final Image newimg = img.getScaledInstance(230, 140,  java.awt.Image.SCALE_SMOOTH);
			final JLabel label2 = new JLabel(new ImageIcon(newimg));
			final JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			panel.setPreferredSize(new Dimension(350, 210));
			final GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0;
			constraints.anchor = GridBagConstraints.CENTER;
			panel.add(label1, constraints);
			constraints.gridy++;
			constraints.gridy++;
			constraints.gridy++;
			constraints.insets = new Insets(20,0,0,20);
			panel.add(label2, constraints);
			return JOptionPane.showInputDialog(null, panel, ((TextInputCallback)callBack).getText(), JOptionPane.PLAIN_MESSAGE);//"DNI Electr\u00f3nico: Introducci\u00f3n de CAN",

    }
}