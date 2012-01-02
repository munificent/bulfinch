# expect: outer

fn main() {
  var outer = "outer"
  var foo = fn() {
    outer
  }
  foo()
}
