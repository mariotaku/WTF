package org.mariotaku.twidere.api.twitter.model;

import org.mariotaku.twidere.api.twitter.model.Status;
import org.mariotaku.twidere.api.twitter.model.User;

/**
 * Created by mariotaku on 16/4/27.
 */
public class StatusAccessor {
    public static void setUser(Status status, User user) {
        status.user = user;
    }
}
