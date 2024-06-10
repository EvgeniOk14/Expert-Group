package org.example;

public class LazzyInitializationExample
{
    private Resource resource;

    public Resource getResource()
    {
        if (resource == null)
        {
            resource = new Resource();
        }
        return resource;
    }
}
