# ShineButton Library ProGuard Rules

# Keep the custom view and its attributes
-keep class com.sackcentury.shinebuttonlib.ShineButton {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    void set*(***);
    *** get*();
}

# Keep the params class used for animation configuration
-keep class com.sackcentury.shinebuttonlib.ShineView$ShineParams {
    public <fields>;
}

# Keep the listener interface
-keep interface com.sackcentury.shinebuttonlib.ShineButton$OnCheckedChangeListener {
    *;
}
