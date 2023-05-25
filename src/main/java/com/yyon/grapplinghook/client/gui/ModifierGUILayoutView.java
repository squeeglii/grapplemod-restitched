package com.yyon.grapplinghook.client.gui;

// Okay so, why this and not just multiple distinct screens like a sane implementation?
// - keep roughly in-line with the old implementation, with the added features
// - testing if UIs could still dynamically be created for future pop-in menus
// - I cba to figure that out yet.
public enum ModifierGUILayoutView {

    UNKNOWN,
    MAIN,
    HELP,
    CATEGORY_LOCKED,
    CATEGORY_PROPERTIES

}
