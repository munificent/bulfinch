# expect: other

fn foo(a) {
  var b = a
  var c = "not used"
  var d = c
  var e = "other"
  var f = e
  f
}

fn main() {
  foo("arg")
}
