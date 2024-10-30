// Define the superclass
class Animal {
    // Constructor for Animal
    constructor(name) {
        this.name = name
    }

    // Method in the superclass
    speak() {
        return "The animal makes a sound."
    }
}

// Define the subclass that inherits from Animal
class Dog extends Animal {
    // Constructor for Dog
    constructor(name) {
        // Call the superclass constructor
        super(name)
    }

    // Override the speak method
    speak() {
        return this.name + " says: Woof!"
    }
}

// Define another subclass that inherits from Animal
class Cat extends Animal {
    // Constructor for Cat
    constructor(name) {
        // Call the superclass constructor
        super(name)
    }

    // Override the speak method
    speak() {
        return this.name + " says: Meow!"
    }
}

// Create instances of Dog and Cat
myDog = new Dog("Buddy")
myCat = new Cat("Whiskers")

// Use the speak method from both instances
print(myDog.speak())  // Output: Buddy says: Woof!
print(myCat.speak())  // Output: Whiskers says: Meow!

// Show that both Dog and Cat are instances of Animal
print(myDog instanceof Animal)  // Output: true
print(myCat instanceof Animal)  // Output: true
