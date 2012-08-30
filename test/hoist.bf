# Test that local variable definitions are not hoisted to the top of a block.
# expect: outer

fn main() {
  var a = "outer"
  var foo = fn() {
    var b = a
    var a = "inner"
    b
  }
  foo()
}
