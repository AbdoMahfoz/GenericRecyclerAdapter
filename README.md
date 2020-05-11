## Install

Add the maven repo to your project level `build.gradle`
```gradle
allprojects {
   repositories {
      ...
      maven { url 'https://jitpack.io' }
   }
}
```
Add the dependency to your module level `build.gradle`
```gradle
dependencies {
   implementation 'com.github.AbdoMahfoz:GenericRecyclerAdapter:1.0.0'
}
```

## Usage

I will add more usage details later, for now, you can use this component to make a `RecyclerView.Adapter` in a single line
```kt
val adapter = GenericRecyclerView.create<Item, ItemViewBinding>(R.layout.item_view)
```
where `Item` implements the interface `GenericRecyclerEntity` and `ItemViewBinding` is the data binding object of `R.layout.item_view`

Ofcourse the exact names are not relevant, just supply an item that implements `GenericRecyclerEntity`, the binding class of your item view (the view used to describe an item in the recycler view), and the layout resource file of that binding.

The library expects that your layout has a databinding variable called `data` and that it's type matches the type of the `Item` type you supplied to the create function

you can then supply the list to the adapter
```kt
val list = listOf<Item>()
adapter.submitList(list)
```
And the adapter will push each element to the `data` variable of your view, and that's it!

## When to use it

It would only be helpful when you just want to have a list of items appear with recycler view, all of which are of the same view and are not interactable

More use cases will be added soon
