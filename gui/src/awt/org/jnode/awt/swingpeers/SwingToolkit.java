/*
 * $Id$
 */
package org.jnode.awt.swingpeers;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;

import javax.swing.JComponent;

import org.jnode.awt.JNodeToolkit;

/**
 * AWT toolkit implemented entirely with JFC peers, thus allowing a lightweight
 * simulation of the operating system desktop.
 */

public class SwingToolkit extends JNodeToolkit {

    private DesktopFrame desktopFrame;

    /**
     * Initialize this instance.
     *  
     */
    public SwingToolkit() {
    }

    // Peers

    protected ButtonPeer createButton(Button target) {
        return new SwingButtonPeer(target);
    }

    protected CanvasPeer createCanvas(Canvas target) {
        //return super.createCanvas( target );
        return new SwingCanvasPeer(target);
    }

    protected CheckboxPeer createCheckbox(Checkbox target) {
        return new SwingCheckboxPeer(target);
    }

    protected CheckboxMenuItemPeer createCheckboxMenuItem(
            CheckboxMenuItem target) {
        return new SwingCheckboxMenuItemPeer(target);
    }

    protected ChoicePeer createChoice(Choice target) {
        return new SwingChoicePeer(target);
    }

    protected LightweightPeer createComponent(Component target) {
        return new SwingLightweightPeer(target);
    }

    protected DialogPeer createDialog(Dialog target) {
        return new SwingDialogPeer(target);
    }

    public DragSourceContextPeer createDragSourceContextPeer(
            DragGestureEvent dge) {
        return null;
    }

    protected FileDialogPeer createFileDialog(FileDialog target) {
        return null;
    }

    protected FramePeer createFrame(Frame target) {
    	setTop(target);
        final int rc = incRefCount();
        if (target instanceof DesktopFrame) {
            log.debug("createFrame:desktopFramePeer(" + target + ")");
            // Only desktop is real frame
            return new DesktopFramePeer(this, target);
        } else {
            log.debug("createFrame:normal(" + target + ")");
            // Other frames are emulated
            return new SwingFramePeer(this, desktopFrame.getDesktop(), target);
        }
    }

    protected LabelPeer createLabel(Label target) {
        return new SwingLabelPeer(target);
    }

    protected ListPeer createList(java.awt.List target) {
        return new SwingListPeer(target);
    }

    protected MenuPeer createMenu(Menu target) {
        return new SwingMenuPeer(target);
    }

    protected MenuBarPeer createMenuBar(MenuBar target) {
        return new SwingMenuBarPeer(target);
    }

    protected MenuItemPeer createMenuItem(MenuItem target) {
        return new SwingMenuItemPeer(target);
    }

    protected PanelPeer createPanel(Panel target) {
        return new SwingPanelPeer(target);
    }

    protected PopupMenuPeer createPopupMenu(PopupMenu target) {
        return new SwingPopupMenuPeer(target);
    }

    protected ScrollbarPeer createScrollbar(Scrollbar target) {
        return new SwingScrollbarPeer(target);
    }

    protected ScrollPanePeer createScrollPane(ScrollPane target) {
        return new SwingScrollPanePeer(target);
    }

    protected TextAreaPeer createTextArea(TextArea target) {
        return new SwingTextAreaPeer(target);
    }

    protected TextFieldPeer createTextField(TextField target) {
        return new SwingTextFieldPeer(target);
    }

    protected WindowPeer createWindow(Window target) {
        return new SwingWindowPeer(target);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Private

    final void onDisposeFrame() {
        decRefCount(false);
    }
    
    /**
     * @see org.jnode.awt.JNodeToolkit#onClose()
     */
    protected void onClose() {
        log.debug("onClose");
        desktopFrame.dispose();
        desktopFrame = null;
    }

    /**
     * @see org.jnode.awt.JNodeToolkit#onInitialize()
     */
    protected void onInitialize() {
        log.debug("onInitialize");
        desktopFrame = new DesktopFrame(getScreenSize());
        desktopFrame.show();
    }
    
    /**
     * Copies the generic component properties from the AWT component into the peer.
     * @param awtComponent
     * @param peer
     */
    final static void copyAwtProperties(Component awtComponent, JComponent peer) {
    	Color c;
    	Font f;
    	if ((c = awtComponent.getForeground()) != null) {
    		peer.setForeground(c);
    	}
    	if ((c = awtComponent.getBackground()) != null) {
    		peer.setBackground(c);
    	}
    	if ((f = awtComponent.getFont()) != null) {
    		peer.setFont(f);
    	}
    }

	public static void add(Component component, JComponent peer) {
		final SwingContainerPeer containerPeer = getContainerPeer(component);
		if (containerPeer != null) {
			containerPeer.addAWTComponent(component, peer);
		}
	}

	private static SwingContainerPeer getContainerPeer(Component component) {
		final Component parent = component.getParent();
		if (parent == null) {
			return null;
		} else {
			final ComponentPeer parentPeer = parent.getPeer();
			if (parentPeer instanceof SwingContainerPeer) {
				return (SwingContainerPeer) parentPeer;
			} else {
				return getContainerPeer(parent);
			}
		}
	}

}