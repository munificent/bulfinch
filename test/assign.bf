# expect: d
fn foo(a, b) {
  b
}

fn main() {
  var a = "a"
  var b = "b"
  b = foo("c", "d")
  b
}
