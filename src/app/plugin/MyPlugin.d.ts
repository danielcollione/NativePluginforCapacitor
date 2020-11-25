declare module "@capacitor/core" {
    interface PluginRegistry {
        MyPlugin: MyPlugin;
    }
}

declare interface Echo{
    value: string;
}

export interface MyPlugin{
    echo(): Promise<Echo>
    getContacts(filter: string): Promise<{results: any[]}>;
}