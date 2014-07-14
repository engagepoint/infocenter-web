package org.eclipse.help.base;

import org.eclipse.help.IIndexEntry;
import org.eclipse.help.IToc;
import org.eclipse.help.ITopic;

import java.util.Locale;

/**
 * Created by yaroslav on 7/14/14.
 */
public abstract class AbstractHelpScope {

    public abstract String getName(Locale arg0);

    /**
     * Determine whether a table of contents is in scope
     */
    public abstract boolean inScope(IToc toc);

    /**
     * Determine whether a topic is in scope
     */
    public abstract boolean inScope(ITopic topic);

    /**
     * Determine whether an index entry is in scope
     */
    public abstract boolean inScope(IIndexEntry entry);


}
