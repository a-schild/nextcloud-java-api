package org.aarboard.nextcloud.api.utils;

import java.util.concurrent.CompletableFuture;

import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.exception.NextcloudOperationFailedException;

public class NextcloudResponseHelper
{
    public static final int NC_OK= 100; // Nextcloud OK message

    private NextcloudResponseHelper() {
    }

    public static <A extends NextcloudResponse> A getAndCheckStatus(CompletableFuture<A> answer)
    {
        A wrappedAnswer = getAndWrapException(answer);
        if(isStatusCodeOkay(wrappedAnswer))
        {
            return wrappedAnswer;
        }
        throw new NextcloudOperationFailedException(wrappedAnswer.getStatusCode(), wrappedAnswer.getMessage());
    }

    public static <A extends NextcloudResponse> boolean isStatusCodeOkay(CompletableFuture<A> answer)
    {
        return isStatusCodeOkay(getAndWrapException(answer));
    }

    public static  boolean isStatusCodeOkay(NextcloudResponse answer)
    {
        return answer.getStatusCode() == NC_OK;
    }

    public static <A> A getAndWrapException(CompletableFuture<A> answer)
    {
        try {
            return answer.get();
        } catch (Exception e) {
            throw new NextcloudApiException(e);
        }
    }
}
